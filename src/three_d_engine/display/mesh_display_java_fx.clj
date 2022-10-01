(ns three-d-engine.display.mesh-display-java-fx
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-meshes :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-projection :refer :all]
            [three-d-engine.3d-util :refer :all]
            [three-d-engine.display.rotation-animation :refer [rotation-animation]]
            [three-d-engine.display.manual-rotation :refer [manual-rotation]]
            [cljfx.api :as fx])
  (:import (javafx.scene.paint Color)
           (javafx.application Platform)))

(def animation manual-rotation)

(def base-mesh (import-mesh-from "teapot-low-poly.obj"))
(def mesh-color {:r 62 :g 126 :b 88})

(def *mesh-state
  (atom {:moved-mesh (project-to-3d (translate-mesh-by-z base-mesh 8))}))

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
   :title            "Mesh"
   :on-close-request (fn [e]
                       ((:finish animation))
                       (Platform/exit))
   :scene            {:fx/type :scene
                      :on-scroll (fn [e] ((:handle-scroll animation) e))
                      :on-key-pressed (fn [e] ((:handle-key-press animation) e))
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

(defn update-mesh-state [mesh]
  (swap!
    *mesh-state assoc
    :moved-mesh mesh))

(defn handle-new-animation-frame [mesh]
  (update-mesh-state (project-to-3d mesh)))

(defn -main[& args]
  (fx/mount-renderer *mesh-state renderer)
  ((:start animation) base-mesh handle-new-animation-frame))

