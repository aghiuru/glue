(ns glue.dancer
  (:require [quil.core :as q :include-macros true]))

(def SIZE 1200)

(def images (ref nil))
(def queue (atom []))

(def image-files
  (let [f (clojure.java.io/file "resources/images")]
    (map #(str "resources/images/" (.getName %)) (drop 1 (file-seq f)))))

(defn load-images []
  (map q/load-image image-files))

(defn random-image []
  {:type :image
   :image (rand-nth @images)
   :x (- (rand-int SIZE) 400)
   :y (- (rand-int SIZE) 400)})

(defn within-bounds [c]
  (and (< 0 c) (< c SIZE)))

(defn repeat-offset [{:keys [x y] :as to-draw} n off-x off-y]
  (->> (range n)
       (map (fn [n] {:x (-> n (* off-x) (+ x))
                     :y (-> n (* off-y) (+ y))}))
       (filter #(and (within-bounds (:x %))
                     (within-bounds (:y %))))
       (map (partial merge to-draw))))

(defn random-image-with-offset []
  (let [image (random-image)
        n (+ 5 (rand-int 10))
        offset-x (rand-int 60)
        offset-y (- (rand-int 200) 100)]
    (repeat-offset image n offset-x offset-y)))

(defn shape [type]
  (let [size (rand-int 500)]
    {:type :shape
     :shape type
     :x (rand-int SIZE)
     :y (rand-int SIZE)}))

(defn band []
  (if (= 0 (rand-int 2))
    {:type :shape
     :shape :rect
     :x 0
     :y (rand-int SIZE)
     :w SIZE
     :h (rand-int (quot SIZE 3))}
    {:type :shape
     :shape :rect
     :x (rand-int SIZE)
     :y 0
     :w (rand-int (quot SIZE 3))
     :h SIZE}))

(defmulti draw-shape (fn [to-draw] (:shape to-draw)))

(defmethod draw-shape :rect [{:keys [x y w h]}]
  (q/fill (rand-int 255) (rand-int 255) (rand-int 255))
  (q/stroke 0 0 0 0)
  (q/rect x y w h))

(defmethod draw-shape :ellipse [{:keys [x y w h]}]
  (q/fill (rand-int 255) (rand-int 255) (rand-int 255))
  (q/stroke 0 0 0 0)
  (q/ellipse x y w h))

(defn populate-queue []
  (reset! queue (concat  [{:type :background}
                          (random-image)
                          (random-image)
                          (random-image)
                          (band)
                          (band)]
                         (random-image-with-offset)
                          [(band)
                           (random-image)]
                         (random-image-with-offset))))

(defn setup []
  (dosync
   (ref-set images (load-images))
   (populate-queue)))

(defn draw []
  (if (and (= 0 (count @queue)) (q/key-pressed?))
    (populate-queue)
    ;; (swap! queue concat (random-image-with-offset))
    )

  (if (< 0 (count @queue))
    (do
      (let [to-draw (first @queue)]
        (case (:type to-draw)
          :image (q/image (:image to-draw)
                          (:x to-draw)
                          (:y to-draw))
          :shape (draw-shape to-draw)
          :background (q/background (rand-int 255)
                                    (rand-int 255)
                                    (rand-int 255))))

      (swap! queue (partial drop 1))
      (q/display-filter :posterize 3))))

(q/defsketch emerald
  :host "host"
  :size [SIZE SIZE]
  :setup setup
  :draw draw)
