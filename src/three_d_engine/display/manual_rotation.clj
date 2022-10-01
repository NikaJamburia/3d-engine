(ns three-d-engine.display.manual-rotation
  (:require [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.display.mesh-display-java-fx :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-util :refer :all]
            [three-d-engine.3d-projection :refer :all])
  (:import (javafx.scene.input KeyCode)))


(def base-mesh (import-mesh-from "teapot-low-poly.obj"))

(def ^:private projection-params
  (assoc default-projection-params
    :window-size {:width 1366 :height 768}))

(def rotation-step (to-radians 20))
(def zoom-step 5)
(def translation-step 0.3)

(def *rotation-state
  (atom {:x 0 :y 0 :z 0 :fov 90 :x-translation 0 :y-translation 0}))

(defn update-theta [coordinate new-value]
  (swap! *rotation-state assoc coordinate new-value))

(defn apply-rotation-state []
  (let [new-params (assoc projection-params
                     :fov (:fov @*rotation-state)
                     :x-translation (:x-translation @*rotation-state)
                     :y-translation (:y-translation @*rotation-state))]
    (-> base-mesh
        (rotate [(rotation-matrix :x (:x @*rotation-state))
                 (rotation-matrix :y (:y @*rotation-state))
                 (rotation-matrix :z (:z @*rotation-state))])
        (project-to-3d new-params)))
)

(defn key-pressed [e]
  (let [key-code (.getCode e)]
    (cond
      (= (KeyCode/LEFT) key-code) (update-theta :y (- (:y @*rotation-state) rotation-step))
      (= (KeyCode/RIGHT) key-code) (update-theta :y (+ (:y @*rotation-state) rotation-step))
      (= (KeyCode/UP) key-code) (update-theta :x (+ (:x @*rotation-state) rotation-step))
      (= (KeyCode/DOWN) key-code) (update-theta :x (- (:x @*rotation-state) rotation-step))
      (= (KeyCode/Q) key-code) (update-theta :z (+ (:z @*rotation-state) rotation-step))
      (= (KeyCode/E) key-code) (update-theta :z (- (:z @*rotation-state) rotation-step))
      (= (KeyCode/W) key-code) (swap! *rotation-state assoc :y-translation (- (:y-translation @*rotation-state) translation-step))
      (= (KeyCode/S) key-code) (swap! *rotation-state assoc :y-translation (+ (:y-translation @*rotation-state) translation-step))
      (= (KeyCode/D) key-code) (swap! *rotation-state assoc :x-translation (+ (:x-translation @*rotation-state) translation-step))
      (= (KeyCode/A) key-code) (swap! *rotation-state assoc :x-translation (- (:x-translation @*rotation-state) translation-step))
      )
    (render-mesh (apply-rotation-state))))

(defn zoom-out []
  (let [old-value (:fov @*rotation-state)]
    (swap! *rotation-state assoc :fov (+ old-value zoom-step))))

(defn zoom-in []
  (let [old-value (:fov @*rotation-state)
        new-value (if (= 1 old-value)
                    old-value
                    (- old-value zoom-step))]
    (swap! *rotation-state assoc :fov new-value)))

(defn scrolled [e]
  (if (neg? (.getDeltaY e))
    (zoom-out)
    (zoom-in))
  (render-mesh (apply-rotation-state)))

(defn -main[& args]
  (display-mesh-window
    (apply-rotation-state)
    (:window-size projection-params)
    key-pressed
    scrolled
    #()))
