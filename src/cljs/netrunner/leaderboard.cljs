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
    (init-state [this] {:team-leaders @fools/user-scores
                        :team-stats @fools/animal-scores})

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
        [:div.leaderboard
         [:div.panel.blue-shade.content-page
          [:h2 "Leaderboards"]
          [:h3 "Teams"]
          (let [team-stats (om/get-state owner :team-stats)
                maxscore (->> team-stats
                              (sort-by second)
                              reverse
                              first
                              second
                              inc)]
            (for [[team data] (sort-by #(:name (second %)) fools/animal-teams)]
              (let [score (get team-stats team)]
                (list [:div (:name data)]
                      [:div.bar {:style {:width (str (+ 10 (* 100 (/ score maxscore))) "%")}}
                       score]))))

         [:h3 "Leaders"]
          (let [leaders (om/get-state owner :team-leaders)]
            (for [[team {leader :leader nick :nickname}] (sort-by #(:name (second %)) fools/animal-teams)]
              (let [high-score (->> (get leaders team)
                                 (sort-by second)
                                 reverse
                                 first)
                    high-name (first high-score)
                    score (second high-score)]
                [:div.team-leader
                 [:div.avatar (om/build avatar {:username high-name :emailhash "fake"} {:opts {:size 48}})]
                 [:div.username high-name]
                 [:div leader]
                 [:div score " " nick " points"]])
              ))
          [:div {:style {:clear "both"}} " "]
          ]


         ]
        ))))

(defn leaderboard [{:keys [user]} owner]
  (om/component
    (prn "!!!!!???")
   (when user
     (om/build leaderboard-view user))))

(om/root leaderboard app-state {:target (. js/document (getElementById "leaderboard"))})
