(ns three-d-engine.display.manual-rotation
  (:require [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.display.mesh-display-java-fx :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-util :refer :all]
            [three-d-engine.3d-projection :refer :all])
  (:import (javafx.scene.input KeyCode)))


(def base-mesh (import-mesh-from "teapot-low-poly.obj"))

(def rotation-step (to-radians 20))
(def zoom-step 1)

(def *rotation-state
  (atom {:x 0 :y 0 :z 0 :z-translation 8}))

(defn update-theta [coordinate new-value]
  (swap! *rotation-state assoc coordinate new-value))

(defn apply-rotation-state []
  (-> base-mesh
      (rotate [(rotation-matrix :x (:x @*rotation-state))
               (rotation-matrix :y (:y @*rotation-state))
               (rotation-matrix :z (:z @*rotation-state))])
      (project-to-3d (:z-translation @*rotation-state))))

(defn key-pressed [e]
  (let [key-code (.getCode e)]
    (cond
      (= (KeyCode/LEFT) key-code) (update-theta :y (- (:y @*rotation-state) rotation-step))
      (= (KeyCode/RIGHT) key-code) (update-theta :y (+ (:y @*rotation-state) rotation-step))
      (= (KeyCode/UP) key-code) (update-theta :x (+ (:x @*rotation-state) rotation-step))
      (= (KeyCode/DOWN) key-code) (update-theta :x (- (:x @*rotation-state) rotation-step))
      (= (KeyCode/W) key-code) (update-theta :z (+ (:z @*rotation-state) rotation-step))
      (= (KeyCode/S) key-code) (update-theta :z (- (:z @*rotation-state) rotation-step))
      )
    (render-mesh (apply-rotation-state))))

(defn zoom-out []
  (let [old-value (:z-translation @*rotation-state)]
    (swap! *rotation-state assoc :z-translation (+ old-value zoom-step))))

(defn zoom-in []
  (let [old-value (:z-translation @*rotation-state)
        new-value (if (= 1 old-value)
                    old-value
                    (- old-value zoom-step))]
    (swap! *rotation-state assoc :z-translation new-value)))

(defn scrolled [e]
  (if (neg? (.getDeltaY e))
    (zoom-out)
    (zoom-in))
  (render-mesh (apply-rotation-state)))

(defn -main[& args]
  (display-mesh-window (apply-rotation-state) key-pressed scrolled #()))
