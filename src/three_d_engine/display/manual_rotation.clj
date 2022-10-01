(ns three-d-engine.display.manual-rotation
  (:require [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.3d-projection :refer :all])
  (:import (javafx.scene.input KeyCode)))

(def pi (Math/PI))

(defn to-radians [degrees]
  (/ (* degrees pi) 180))

(def rotation-step-degrees (to-radians 20))
(def zoom-step 0.5)

(defn subtract-theta [theta n]
  (- theta n))

(defn add-theta [theta n]
  (+ theta n))

(def *rotation-state
  (atom {:x 0 :y 0 :z 0 :z-translation 8}))

(defn update-theta [coordinate new-value]
  (swap! *rotation-state assoc coordinate new-value))

(defn apply-rotation-state []
  (let [mesh (:base-mesh @*rotation-state)]
    (-> mesh
        (rotate [(rotation-matrix :x (:x @*rotation-state))
                      (rotation-matrix :y (:y @*rotation-state))
                      (rotation-matrix :z (:z @*rotation-state))])
        (translate-mesh-by-z (:z-translation @*rotation-state)))))

(defn notify-state-updated []
  ((:new-frame-callback @*rotation-state) (apply-rotation-state)))

(defn key-pressed [e]
  (let [key-code (.getCode e)]
    (cond
      (= (KeyCode/LEFT) key-code) (update-theta :y (subtract-theta (:y @*rotation-state) rotation-step-degrees))
      (= (KeyCode/RIGHT) key-code) (update-theta :y (add-theta (:y @*rotation-state) rotation-step-degrees))
      (= (KeyCode/UP) key-code) (update-theta :x (add-theta (:x @*rotation-state) rotation-step-degrees))
      (= (KeyCode/DOWN) key-code) (update-theta :x (subtract-theta (:x @*rotation-state) rotation-step-degrees))
      (= (KeyCode/W) key-code) (update-theta :z (add-theta (:z @*rotation-state) rotation-step-degrees))
      (= (KeyCode/S) key-code) (update-theta :z (subtract-theta (:z @*rotation-state) rotation-step-degrees))
      )
    (notify-state-updated)))

(defn zoom-out []
  (let [old-value (:z-translation @*rotation-state)]
    (swap! *rotation-state assoc :z-translation (+ old-value zoom-step))
    (notify-state-updated)))

(defn zoom-in []
  (let [old-value (:z-translation @*rotation-state)
        new-value (if (= 1 old-value)
                    old-value
                    (- old-value zoom-step))]
    (println old-value new-value)
    (swap! *rotation-state assoc :z-translation new-value)
    (notify-state-updated)))

(defn scrolled [e]
  (if (neg? (.getDeltaY e))
    (zoom-out)
    (zoom-in)))

(defn start [base-mesh on-new-frame]
  (swap! *rotation-state assoc :new-frame-callback on-new-frame)
  (swap! *rotation-state assoc :base-mesh base-mesh))

(def manual-rotation {:start start
                      :handle-key-press key-pressed
                      :handle-scroll scrolled
                      :finish #()})
