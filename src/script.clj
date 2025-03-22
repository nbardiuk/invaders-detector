(ns script
  (:require
    [clojure.string :as str]
    [clojure.tools.cli :refer [parse-opts]]
    [detector])
  (:gen-class))

(defn- read-radar [file]
  (->> (slurp file)
       str/lower-case
       str/split-lines
       (remove #(str/starts-with? % "~"))
       vec))

(defn- read-invaders [file]
  (->> (slurp file)
       str/lower-case
       (#(str/split % #"~+"))
       (mapv str/split-lines)
       (mapv #(filterv (complement empty?) %))
       (filterv (complement empty?))))

(defn- validate-image [image]
  (every? #{\- \o} (mapcat seq image)))

(defn- validate-invaders [invaders]
  (every? validate-image invaders))

(defn- format-found-invader
  [{:keys [x y width height similarity image]}]
  (let [header (format "Found invader at {x: %d, y: %d, width: %d, height: %d} with similarity %.3f: "
                       x y width height (double similarity))]
    (-> [header] (into image) (conj ""))))

(def cli-options
  [["-r" "--radar" "Path to radar file"
    :parse-fn read-radar
    :missing "Radar file is required"
    :required "FILE"
    :validate [validate-image "Must contain only `-` and `o` characters"]]

   ["-i" "--invaders" "Path to invaders patterns file"
    :missing "Invaders file is required"
    :required "FILE"
    :parse-fn read-invaders
    :validate [validate-invaders "Must contain only `-` and `o` characters"]]

   ["-t" "--threshold" "Threshold for similarity with invader pattern"
    :required "NUMBER"
    :parse-fn parse-double
    :validate [#(<= 0 % 1) "Must be a number between 0 and 1"]
    :default 0.75]])

(defn- format-cli-error [errors summary]
  (into errors ["" "Usage:" summary]))


(defn -main [& args]
  (let [{:keys [errors summary options]} (parse-opts args cli-options :strict true)
        {:keys [threshold radar invaders]} options]
    (if (seq errors)
      (run! println (format-cli-error errors summary))
      (if-let [found (seq (detector/detect-all threshold radar invaders))]
        (run! println (mapcat format-found-invader found))
        (println "No invaders found")))))
