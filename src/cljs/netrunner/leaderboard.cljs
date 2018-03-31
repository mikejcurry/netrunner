(ns netrunner.leaderboard
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as sab :include-macros true]
            [cljs.core.async :refer [chan put!] :as async]
            [clojure.string :as s]
            [goog.dom :as gdom]
            [netrunner.auth :refer [authenticated avatar] :as auth]
            [netrunner.appstate :refer [app-state]]
            [netrunner.ajax :refer [POST GET PUT]]
            [jinteki.cards :refer [all-cards]]
            [jinteki.fools :as fools]
            [netrunner.ws :as ws]))

(defn leaderboard-view [leaderboard owner]
  (reify
    om/IInitState
    (init-state [this] (let [stats (fools/socket-data)]
                         {:team-leaders (:leaders stats)
                          :team-stats (:teams stats)}))

    om/IWillMount
    (will-mount [this]
      (ws/register-ws-handler!
        :fools/stats
        #(do (om/set-state! owner :team-leaders (:leaders %))
             (om/set-state! owner :team-stats (:teams %))
             (reset! fools/animal-scores (:teams %))
             (reset! fools/user-scores (:leaders %)))))

    om/IRenderState
    (render-state [this state]
      (sab/html
        [:div.leaderboard.panel.content-page.blue-shade
         [:h2 "Leaderboards"]
         [:h3 "Teams"]
         (let [team-stats (om/get-state owner :team-stats)
               maxscore (max 1
                             (->> team-stats
                               (sort-by second)
                               reverse
                               first
                               second
                               inc))]
           (for [[team data] (sort-by #(:name (second %)) fools/animal-teams)]
             (let [score (get team-stats team 0)
                   shortname (last (s/split (:name data) #" "))]
               (list [:div.teamname (:name data)]
                     [:div.bar
                      {:class shortname
                       :style
                       {:width (str (+ 10 (* 100 (/ score maxscore))) "%")}}
                      [:span.score score]]))))

         [:h3 "Pack Leaders"]
         (let [leaders (om/get-state owner :team-leaders)]
           (for [[team {leader :leader nick :nickname}] (sort-by #(:name (second %)) fools/animal-teams)]
             (let [high-score (get leaders team)
                   high-name (first high-score)
                   score (second high-score)]
               (when score
                 [:div.team-leader
                  [:div.avatar (om/build avatar {:username high-name :emailhash "fake"} {:opts {:size 48}})]
                  [:div.username high-name]
                  [:div leader]
                  [:div score " " nick " points"]]))))
         [:div {:style {:clear "both"}} " "]]))))

(defn leaderboard [{:keys [user]} owner]
  (om/component
   (when user
     (om/build leaderboard-view user))))

(om/root leaderboard app-state {:target (. js/document (getElementById "leaderboard"))})
