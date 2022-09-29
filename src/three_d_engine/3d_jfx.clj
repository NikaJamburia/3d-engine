(ns three-d-engine.3d-jfx
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-meshes :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-projection :refer :all]
            [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.3d-util :refer :all]
            [three-d-engine.3d-options-menu :refer :all]
            [cljfx.api :as fx])
  (:import (javafx.scene.paint Color)
           (java.util Timer TimerTask)
           (javafx.application Platform)))

(def base-mesh (import-mesh-from "teapot-low-poly.obj"))

(def repaint-millis 30)
(def start-millis (System/currentTimeMillis))
(def timer (new Timer))

(def *options (atom {:color {:r 62 :g 126 :b 88}
                     :rotation-stopped-at-theta nil}))

(defn get-current-rotation-theta []
  (if (= nil (:rotation-stopped-at-theta @*options))
    (/ (- (System/currentTimeMillis) start-millis) 1500)
    (:rotation-stopped-at-theta @*options)))

(defn mesh-to-display [mesh theta]
  (-> mesh
      (rotate [(rotation-matrix :z theta)
               (rotation-matrix :x (* 0.5 theta))])
      (project-to-3d)))

(def *mesh-state
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
        color (adjust-color (:color @*options) (:lighting triangle))]
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
                       (.cancel timer)
                       (.purge timer)
                       (Platform/exit))
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

(defn update-mesh-state []
  (let [time-elapsed (get-current-rotation-theta)]
    (swap!
      *mesh-state assoc
      :moved-mesh (mesh-to-display base-mesh time-elapsed))))

(def repaint-mesh-task (proxy [TimerTask] []
                  (run []
                    (update-mesh-state))))

(defn options-menu []
  (display-options-menu
    (fn [color]
      (swap! *options assoc :color color)
      (update-mesh-state))
    (fn []
      (swap! *options assoc :rotation-stopped-at-theta (get-current-rotation-theta)))
    (fn []
      (swap! *options assoc :rotation-stopped-at-theta nil))))

(defn -main[& args]
  (fx/mount-renderer *mesh-state renderer)
  (.schedule timer repaint-mesh-task repaint-millis repaint-millis)
  ;(options-menu)
  )
