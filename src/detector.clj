(ns detector
  (:require
    [clojure.string :as str]))

(defn- image-size [image]
  {:width (count (first image))
   :height (count image)})

(defn- subimage
  [image {:keys [x y width height]}]
  (let [total-size (image-size image)]
    (for [row (range y (min (+ y height) (:height total-size)))]
      (str/join (take width (drop x (image row)))))))

(defn- all-subimages [image size]
  (let [{:keys [width height]} (image-size image)]
    (for [x (range 0 width)
          y (range 0 height)
          :let [area (assoc size :x x :y y)
                subimage (subimage image area)]]
      (assoc area :image subimage))))

(defn- similarity
  [invader {:keys [image width height]}]
  (let [matching-pixels (->> (map = (mapcat seq invader) (mapcat seq image))
                             (filter identity)
                             count)
        total-pixels (* width height)]
    (/ matching-pixels total-pixels)))

(defn- detect [radar invader]
  (->>
    (all-subimages radar (image-size invader))
    (map #(assoc % :similarity (similarity invader %)))))

(defn detect-all
  [threshold radar invaders]
  (->> invaders
       (mapcat #(detect radar %))
       (filter #(<= threshold (:similarity %)))
       (sort-by (juxt (comp - :similarity) :x :y))))
