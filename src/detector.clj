(ns detector
  (:require
    [clojure.string :as str]))

(defn- image-size [image]
  {:width (count (first image))
   :height (count image)})

(defn- extract
  [image [x y] area]
  (let [size (image-size image)]
    (for [i (range y (min (+ y (:height area)) (:height size)))]
      (str/join (take (:width area) (drop x (image i)))))))

(defn detect [invader radar]
  (let [invader-size (image-size invader)
        radar-size (image-size radar)]
    (for [x (range 0 (:width radar-size))
          y (range 0 (:height radar-size))
          :when (= invader (extract radar [x y] invader-size))]
      [x y])))
