(ns glue.core
  (:require [mikera.image.core :as img]
            [mikera.image.filters :as filt])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; load an image from a resource file
(def ant (img/load-image-resource "images/Screen_Shot_2017-10-09_at_12.44.46_PM.png"))

;; show the iamge, after applying an "invert" filter
(defn show []
  (img/show (-> ant
                (img/sub-image 10 10 100 100)
                (img/filter-image (filt/noise)))))
