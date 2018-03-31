(ns jinteki.fools)

(def animal-teams {:dog      {:name      "Team Dog"
                              :cards     #{"Lycan" "Synth DNA Modification" "Rover Algorithm" "Watchdog" "Lab Dog"
                                           "Cerberus \"Lady\" H1" "Cerberus \"Rex\" H2" "Cerberus \"Cuj.0\" H3"
                                           "Pup" "Komainu" "News Hound" "Emergency Shutdown" "HQ Interface" "Data Hound"
                                           "Grim"}
                              :card-icon "🐶"
                              :nickname  "bow-wow"
                              :leader    "Good Boy"}
                   :cat      {:name      "Team Cat"
                              :cards     #{"BlacKat" "Rumor Mill" "Marathon" "Hellion Alpha Test" "Hellion Beta Test" "Quantum Predictive Model"
                                           "Chimera" "Quandary" "Conundrum"}
                              :card-icon "🐱"
                              :nickname  "kitty meow"
                              :leader    "Something something"
                              :sounds    {:trash #{"cat-trash1"}
                                          :play #{"cat-play1"}
                                          }}
                   :snake    {:name      "Team Snake"
                              :cards     #{"Susanoo-no-Mikoto" "Cobra" "Viper" "Mamba" "Caduceus" "Puffer" "Lamprey" "Wormhole" "Wraparound" "Tollbooth" "Uroboros" "Dracō" "Wyrm"}
                              :card-icon "🐍"
                              :nickname  "snek"
                              :leader    "Cobra Commander"
                              :sounds    {:play #{"snake-play1"}}}
                   :lizard   {:name      "Team Lizard"
                              :cards     #{"Financial Collapse" "Shattered Remains" "Casting Call" "Chameleon" "Exploratory Romp" "Modded"
                                           "Maw" "Dinosaurus"}
                              :card-icon "🦎"
                              :nickname  "dinorawr"
                              :leader    "GOJIRA!!!!"
                              :sounds    {:play #{"lizard-play1"}}}
                   :turtle   {:name      "Team Turtle"
                              :cards     #{"Aumakua" "Turtlebacks" "Shell Corporation" "Bullfrog" "Gbahali"}
                              :card-icon "🐢"
                              :nickname  "cowabunga"
                              :leader    "TURTLE POWER"
                              :sounds    {:play  #{"turtle-play1"}
                                          :use   #{"turtle-use1"}
                                          :trash #{"turtle-trash"}}}
                   :ungulate {:name      "Team Ungulate"
                              :cards     #{"Celebrity Gift" "Trojan Horse" "Patron" "Green Level Clearance" "Improved Tracers"
                                           "Taurus" "On the Lam" "Battering Ram" "Wari" "Knight"}
                              :card-icon "🐮"
                              :nickname  "whinnie"
                              :leader    "Li'l Sebastian"
                              :sounds    {}}
                   :bee      {:name      "Team Bee"
                              :cards     #{"Plan B" "Honeyfarm" "Chrysalis" "Hive" "Hivemind" "Swarm" "Mutate" "Interrupt 0" "Special Report"
                                           "Bug"}
                              :card-icon "🐝"
                              :nickname  "buzz buzz"
                              :leader    "Bee the Change You Want to See In the World"}
                   :bird     {:name      "Team Bird"
                              :cards     #{"Owl" "Firmware Updates" "Kongamato" "Golden" "Peregrine" "Saker" "Recon Drone" "Origami"
                                           "GS Shrike M2" "Data Raven" "Peacock" "Egret"}
                              :card-icon "🐦"
                              :nickname  "birb"
                              :leader    "Wingardium Leviosa"
                              :sounds    {:play  #{"bird-play1" "bird-play2"}
                                          :use   #{"bird-use1"}
                                          :trash #{"bird-trash1" "bird-trash2"}}}
                   :whale    {:name      "Team Whale"
                              :cards     #{"Howler" "Darwin" "Swordsman" "Red Herrings" "Spear Phishing" "Leviathan"}
                              :card-icon "🐳"
                              :nickname  "baby beluga"
                              :leader    ""
                              :sounds    {:trash #{"whale-trash"}
                                          :play  #{"whale-play1" "whale-play2"}}}})


(defonce animal-scores (atom (zipmap
                               (keys animal-teams)
                               (replicate (count animal-teams) 0))))

(defonce user-scores (atom {}))

(defn team-cards [team]
  (get-in animal-teams [team :cards]))

(defn team-nickname [team]
  (get-in animal-teams [team :nickname]))

(defn team-name [team]
  (get-in animal-teams [team :name]))

(defn team-leader [team]
  (get-in animal-teams [team :leader]))

(defn team-card-icon [team]
  (get-in animal-teams [team :card-icon]))

(defn card-team [card-title]
  (some #(when (contains? (:cards (second %)) card-title)
           (first %))
        animal-teams))

(defn parse-name [s]
  #?(:clj  (reduce + (map int s))
     :cljs (reduce + (map #(.charCodeAt % 0) s))))

(defn animal-team [{:keys [username] :as user}]
  (nth (keys animal-teams)
       (mod (parse-name username)
            (count animal-teams))))

(defn animal-username [{:keys [username] :as user}]
  (let [team (animal-team user)
        leader (first (get @user-scores team))]
    (if (= leader username)
      (str username " " (team-card-icon team) (team-card-icon team) (team-card-icon team)
           " [" (team-leader team) "]")
      (str username " " (team-card-icon team)))))

(defn increment-animal-score
  "Takes a team name as a symbol, and increments that team's score by 1."
  ([team] (increment-animal-score team 1))
  ([team amount] (swap! animal-scores update-in [team] (partial + amount))))

(defn increment-user-score
  "Increments the user's core for the given team"
  ([team user] (increment-user-score team user 1))
  ([team {:keys [username] :as user} amount]
   (swap! user-scores update-in [team username] (fnil inc 0))))

(defn get-score
  "Takes a team name as a symbol, and returns that team's score."
  [team]
  (@animal-scores team))

(defn two-player-game? [state]
  (and (-> @state :corp :user :username) (-> @state :runner :user :username))
  true) ;; TODO: REMOVE THIS

(defn score-card-use [state side card]
  (let [user (get-in @state [side :user])
        user-animal (animal-team user)
        card-animal (card-team (:title card))]
    (when (two-player-game? state)
      (increment-animal-score card-animal)
      (when (= user-animal card-animal)
        (increment-user-score user-animal user)))))

(defn score-misc [state side amount]
  (let [user (get-in @state [side :user])
        user-animal (animal-team user)]
    (when (two-player-game? state)
      (increment-user-score user-animal user amount))))

(defn high-score [team]
  (first (get @user-scores team)))

(defn- fake-user [username]
  (atom {:runner {:user {:username username}}
         :corp {:user {:username "Fake"}}}))

(do #?(:clj (do
              (increment-animal-score :turtle)
              (score-card-use (fake-user "nealpro")
                              :runner {:title "Patron"})
              (score-card-use (fake-user "Saintis")
                              :runner {:title "Data Raven"})
              (score-card-use (fake-user "bobtomatoes")
                              :runner {:title "Pup"})
              (score-card-use (fake-user "mtgred")
                              :runner {:title "Pup"})
              (score-card-use (fake-user "bobtomatoes")
                              :runner {:title "Pup"})
              (score-card-use (fake-user "domtancredi")
                              :runner {:title "Hive"})
              (score-card-use (fake-user "danhut")
                              :runner {:title "Maw"})
              (score-card-use (fake-user "Msbeck")
                              :runner {:title "Viper"})
              (score-card-use (fake-user "presheaf")
                              :runner {:title "Hellion Alpha Test"})
              (score-card-use (fake-user "Aumakua")
                              :runner {:title "Aumakua"})
              (score-card-use (fake-user "JoelCFC25")
                              :runner {:title "Leviathan"}))))

(defn socket-data []
  {:teams @animal-scores
   :leaders (into {} (map (fn [[team scores]]
                            [team
                             (first (reverse (sort-by second scores)))])
                          @user-scores))})