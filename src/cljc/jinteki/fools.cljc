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
                              :leader    "Oprah Winfrey"}
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
        leader (first (first (get @user-scores team)))]
    (if (= leader username)
      (str "[" (team-leader team) "] " username)
      (str "[" (team-name team) "] " username))))


(defn increment-animal-score
  "Takes a team name as a symbol, and increments that team's score by 1."
  [team]
  (swap! animal-scores update-in [team] inc))

(defn increment-user-score [team {:keys [username] :as user}]
  (swap! user-scores update-in [team username] (fnil inc 0)))

(defn get-score
  "Takes a team name as a symbol, and returns that team's score."
  [team]
  (@animal-scores team))

(defn record-score [state side card]
  (let [{:keys [username] :as user} (get-in @state [side :user])
        user-animal (animal-team user)
        card-animal (card-team (:title card))]
    (when (= user-animal card-animal)
      (increment-animal-score user-animal)
      (increment-user-score user-animal user))))

(defn high-score [team]
  (first (get @user-scores team)))


(increment-animal-score :turtle)
(increment-animal-score :turtle)
(record-score (atom {:runner {:user {:username "nealpro"}}})
              :runner {:title "Patron"})
(record-score (atom {:runner {:user {:username "Saintis"}}})
              :runner {:title "Data Raven"})
(record-score (atom {:runner {:user {:username "bobtomatoes"}}})
              :runner {:title "Pup"})
(record-score (atom {:runner {:user {:username "bobtomatoes"}}})
              :runner {:title "Pup"})
(record-score (atom {:runner {:user {:username "domtancredi"}}})
              :runner {:title "Hive"})
(record-score (atom {:runner {:user {:username "danhut"}}})
              :runner {:title "Maw"})
(record-score (atom {:runner {:user {:username "Msbeck"}}})
              :runner {:title "Viper"})
(record-score (atom {:runner {:user {:username "presheaf"}}})
              :runner {:title "Hellion Alpha Test"})
(record-score (atom {:runner {:user {:username "giesch7"}}})
              :runner {:title "Aumakua"})

(record-score (atom {:runner {:user {:username "JoelCFC25"}}})
              :runner {:title "Leviathan"})



