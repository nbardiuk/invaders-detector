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

(defn detect [radar invader]
  (->>
    (all-subimages radar (image-size invader))
    (filter #(= invader (:image %)))
    (map #(dissoc % :image))))

(defn detect-all [radar invaders]
  (mapcat #(detect radar %) invaders))
