(ns three-d-engine.3d-jfx
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-meshes :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-projection :refer :all]
            [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.3d-util :refer :all]
            [cljfx.api :as fx])
  (:import (javafx.scene.paint Color)
           (java.util Timer TimerTask)))

(def base-mesh (import-mesh-from "teapot-low-poly.obj"))
(def mesh-color {:r 62 :g 126 :b 88})

(def start-millis (System/currentTimeMillis))
(def repaint-millis 30)

(defn mesh-to-display [mesh rotation-theta]
  (-> mesh
      (rotate-mesh rotation-theta)
      (project-to-3d)))

(def *state
  (atom {:moved-mesh (mesh-to-display base-mesh 0)}))

(defn adjust-color-part [part lighting]
  (let [abs-value (Math/abs (float lighting))
        multiplied (Math/ceil (* abs-value part))]
    (if (> multiplied 255) 255 (int multiplied))))

(defn adjust-color [color lighting]
  (if (neg? lighting)
    (Color/rgb
         (adjust-color-part (:r color) lighting)
         (adjust-color-part (:g color) lighting)
         (adjust-color-part (:b color) lighting))
    (Color/BLACK)))

(defn fx-create-triangle [triangle]
  (let [vec (:vectors triangle)
        color (adjust-color mesh-color (:lighting triangle))]
    {:fx/type :polygon
     :fill color
     :smooth false
     :points  [(:x (first vec)) (:y (first vec))
               (:x (second vec)) (:y (second vec))
               (:x (third vec)) (:y (third vec))]}))

(defn fx-mesh-to-polygons [mesh]
  (map fx-create-triangle (:triangles mesh)))

(defn root [{:keys [moved-mesh]}]
  {:fx/type          :stage
   :showing          true
   :resizable        false
   :min-height       (:height window-size)
   :min-width        (:width window-size)
   :scene            {:fx/type :scene
                      :root    {:fx/type  :pane
                                :children (into [] (concat
                                                     [{:fx/type :rectangle
                                                       :width   (:width window-size)
                                                       :height  (:height window-size)
                                                       :fill    :black}]
                                                     (fx-mesh-to-polygons moved-mesh)))}}})

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)))

(def repaint-mesh-task (proxy [TimerTask] []
                  (run []
                    (let [time-elapsed (/ (- (System/currentTimeMillis) start-millis) 1500)]
                      (swap!
                        *state assoc
                        :moved-mesh (mesh-to-display base-mesh time-elapsed))))))

(defn -main[& args]
  (fx/mount-renderer *state renderer)
  (.schedule (new Timer) repaint-mesh-task repaint-millis repaint-millis))
