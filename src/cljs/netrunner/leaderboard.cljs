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
            [netrunner.ws :as ws]
            [cljs.core.async :refer [chan put! >! sub pub] :as async]))

(def score-channel (chan))


(defn leaderboard-view [leaderboard owner]
  (reify
    om/IInitState
    (init-state [this] (let [stats (fools/socket-data)]
                         {:team-leaders (:leaders stats)
                          :team-stats (:teams stats)
                          :my-score 0}))

    om/IWillMount
    (will-mount [this]
      (go (let [score (<! (GET "/fools/score"))]
            (om/set-state! owner :my-score (:score (:json score)))))

      (go (while true
            (let [score (<! score-channel)]
              (om/set-state! owner :my-score score))))

      (ws/register-ws-handler!
        :fools/stats
        #(do (om/set-state! owner :team-leaders (:leaders %))
             (om/set-state! owner :team-stats (:teams %))
             (reset! fools/animal-scores (:teams %))
             (reset! fools/user-scores (:leaders %)))))

    om/IRenderState
    (render-state [this state]
      (sab/html
        (let [my-team (fools/animal-team (:user @app-state))]
          [:div.leaderboard.panel.content-page.blue-shade
           [:h2 "Leaderboards"]
           [:p {:style {:font-size "13pt"}}
            "Welcome to the Jinteki Natural Wildlife Preserve. Your PAD has been randomly assigned to a hunting pack. "
            "Earn points for your pack and become the new pack leader for glorious rewards."]
           (when my-team
             [:p {:style {:font-size "13pt"}}
              "You have earned " (om/get-state owner :my-score) " " (fools/team-nickname my-team)
              " points for " (fools/team-name my-team) "."])
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
                 (list [:div.teamname (fools/team-card-icon team) " " (:name data) " " (fools/team-card-icon team)]
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
                    [:div.teamname (fools/team-name team) " " (fools/team-card-icon team) (fools/team-card-icon team) (fools/team-card-icon team)]
                    [:div.username high-name " [" (fools/team-leader team) "]"]
                    [:div score " " nick " points"]]))))
           [:div {:style {:clear "both"}} " "]])))))

(defn leaderboard [{:keys [user]} owner]
  (om/component
   (when user
     (om/build leaderboard-view user))))

(om/root leaderboard app-state {:target (. js/document (getElementById "leaderboard"))})
