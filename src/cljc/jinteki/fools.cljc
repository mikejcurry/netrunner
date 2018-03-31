(ns jinteki.fools)

(def animal-teams {:dog      {:name      "Team Dog"
                              ;; First row of cards is corp, second is runner
                              :cards     #{"Lycan" "Synth DNA Modification" "Rover Algorithm" "Watchdog" "Lab Dog" "Pup" "Komainu" "News Hound"  "Data Hound" "Grim"
                                           "Cerberus \"Lady\" H1" "Cerberus \"Rex\" H2" "Cerberus \"Cuj.0\" H3" "Emergency Shutdown" "HQ Interface"}
                              :card-icon "🐶"
                              :nickname  "bow-wow"
                              :leader    "Good Boy"
                              :sounds    {:play  #{"dog-play1"}
                                          :trash #{"dog-trash1"}
                                          :use   #{"dog-use1"}}}
                   :cat      {:name      "Team Cat"
                              :cards     #{"Hellion Alpha Test" "Hellion Beta Test" "Quantum Predictive Model" "Chimera" "Quandary" "Conundrum"
                                           "BlacKat" "Rumor Mill" "Marathon" }
                              :card-icon "🐱"
                              :nickname  "kitty meow"
                              :leader    "Mr. Bigglesworth"
                              :sounds    {:trash #{"cat-trash1" "cat-trash2"}
                                          :play  #{"cat-play1"}
                                          :use   #{"cat-use1" "cat-use2"}}}
                   :snake    {:name      "Team Snake"
                              :cards     #{"Susanoo-no-Mikoto" "Cobra" "Viper" "Mamba" "Caduceus" "Wormhole" "Wraparound" "Tollbooth" "Uroboros" "Dracō"
                                           "Puffer"  "Wyrm" "Lamprey"}
                              :card-icon "🐍"
                              :nickname  "snek"
                              :leader    "Cobra Commander"
                              :sounds    {:play  #{"snake-play1"}
                                          :trash #{"snake-trash1"}
                                          :use   #{"snake-sure1"}}}
                   :lizard   {:name      "Team Lizard"
                              :cards     #{"Financial Collapse" "Shattered Remains" "Casting Call"
                                           "Chameleon" "Exploratory Romp" "Modded" "Maw" "Dinosaurus"}
                              :card-icon "🦎"
                              :nickname  "dinorawr"
                              :leader    "GOJIRA!!!!"
                              :sounds    {:play  #{"lizard-play1" "lizard-play2" "lizard-play3" "lizard-play4" "lizard-play5"}
                                          :use   #{"lizard-use1" "lizard-use2" "lizard-use3" "lizard-use4" "lizard-use5"}
                                          :trash #{"lizard-trash1"}}}
                   :turtle   {:name      "Team Turtle"
                              :cards     #{"Turtlebacks" "Shell Corporation" "Bullfrog"
                                           "Aumakua" "Gbahali"}
                              :card-icon "🐢"
                              :nickname  "cowabunga"
                              :leader    "TURTLE POWER"
                              :sounds    {:play  #{"turtle-play1" "turtle-play2" "turtle-play3" "turtle-play4"}
                                          :use   #{"turtle-use1" "turtle-use2" "turtle-use3" "turtle-use4" "turtle-use5"}
                                          :trash #{"turtle-trash1" "turtle-trash2"}}}
                   :ungulate {:name      "Team Ungulate"
                              :cards     #{"Celebrity Gift" "Trojan Horse" "Green Level Clearance" "Improved Tracers" "Taurus"  "Sagittarius"
                                           "Patron" "On the Lam" "Battering Ram" "Wari" "Knight"}
                              :card-icon "🐮"
                              :nickname  "whinnie"
                              :leader    "Li'l Sebastian"
                              :sounds    {:play  #{"ungulate-play1" "ungulate-play2" "ungulate-play3"}
                                          :trash #{"ungulate-trash1"}
                                          :use   #{"ungulate-use1"}}}
                   :bee      {:name      "Team Bee"
                              :cards     #{"Plan B" "Honeyfarm" "Chrysalis" "Hive" "Swarm" "Mutate" "Interrupt 0" "Special Report"  "Armored Servers"
                                           "Hivemind" "Bug"}
                              :card-icon "🐝"
                              :nickname  "buzz buzz"
                              :leader    "Bee the Change You Want to See In the World"
                              :sounds    {:use   #{"bee-use1" "bee-use2"}
                                          :play  #{"bee-play1" "bee-play2"}
                                          :trash #{"bee-trash1"}}}
                   :bird     {:name      "Team Bird"
                              :cards     #{"Owl" "Firmware Updates" "Data Raven" "Thoth" "Hadrian's Wall"
                                           "Kongamato" "Golden" "Peregrine" "Saker" "Recon Drone" "Origami" "GS Shrike M2" "Peacock" "Egret" "Early Bird" "Paricia" "The Gauntlet"}
                              :card-icon "🐦"
                              :nickname  "birb"
                              :leader    "Wingardium Leviosa"
                              :sounds    {:play  #{"bird-play1" "bird-play2" "bird-play3" "bird-play4" "bird-play5" "bird-play6"
                                                   "bird-play7" "bird-play8" "bird-play9" "bird-play10"}
                                          :use   #{"bird-use1" "bird-use2" "bird-use3" "bird-use4" "bird-use5"}
                                          :trash #{"bird-trash1" "bird-trash2"}}}
                   :whale    {:name      "Team Whale"
                              :cards     #{"Howler" "Swordsman" "Red Herrings"
                                           "Darwin" "Spear Phishing" "Leviathan"}
                              :card-icon "🐳"
                              :nickname  "baby beluga"
                              :leader    "Call Me Ishmael"
                              :sounds    {:trash #{"whale-trash" "whale-trash2" "whale-trash3"}
                                          :play  #{"whale-play1" "whale-play2" "whale-play3" "whale-play4" "whale-play5"}
                                          :use   #{"whale-use1" "whale-use2" "whale-use4"}}}})


(defonce animal-scores (atom (zipmap
                               (keys animal-teams)
                               (replicate (count animal-teams) 0))))

(defonce user-scores (atom {}))

(defonce card-uses (atom {}))

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
  ([team amount] (when-not (nil? team)
                   (swap! animal-scores update-in [team] (partial + amount)))))

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

(defn score-card-use [state side {title :title :as card}]
  (let [{username :username :as user} (get-in @state [side :user])
        user-animal (animal-team user)
        card-animal (card-team title)
        existing-points (get-in @state [side :fools title] 0)]
    (when (and card-animal
               (two-player-game? state)
               (< existing-points 10))
      (increment-animal-score card-animal)
      ;; Record one point for this card during this game.
      (swap! state update-in [side :fools title] (fnil inc 0))
      (when (= user-animal card-animal)
        ;; Record card usage by this player
        (swap! card-uses update-in [user-animal username] #(conj (or % #{}) title))
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