(ns three-d-engine.display.rotation-animation
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-meshes :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-projection :refer :all]
            [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.display.mesh-display-java-fx :refer :all]
            [three-d-engine.3d-util :refer :all])
  (:import (java.util Timer TimerTask)
           (javafx.scene.input KeyCode)))

(def *options (atom {:rotation-stopped-at-theta nil}))

(def repaint-millis 30)
(def start-millis (System/currentTimeMillis))
(def timer (new Timer))

(def base-mesh (import-mesh-from "teapot-low-poly.obj"))

(defn- make-rotation [mesh theta]
  (-> mesh
      (rotate [(rotation-matrix :z theta)
               (rotation-matrix :x (* 0.5 theta))])
      (project-to-3d)))

(defn- get-current-rotation-theta []
  (if (= nil (:rotation-stopped-at-theta @*options))
    (/ (- (System/currentTimeMillis) start-millis) 1500)
    (:rotation-stopped-at-theta @*options)))

(defn- stop-rotation []
  (swap! *options assoc :rotation-stopped-at-theta (get-current-rotation-theta)))

(defn- resume-rotation []
  (swap! *options assoc :rotation-stopped-at-theta nil))

(defn- toggle-animation []
  (if (= nil (:rotation-stopped-at-theta @*options))
    (stop-rotation)
    (resume-rotation)))

(defn- key-pressed [e]
  (println (.getCode e))
  (if (= (KeyCode/SPACE) (.getCode e))
    (toggle-animation)))

(defn- start-rotation-animation []
  (.schedule
    timer
    (proxy [TimerTask] [] (run [] (render-mesh (make-rotation base-mesh (get-current-rotation-theta)))))
    repaint-millis
    repaint-millis))

(defn- finish-animation []
  (.cancel timer)
  (.purge timer))

(defn -main[& args]
  (display-mesh-window
    (make-rotation base-mesh 0)
    (:window-size default-projection-params)
    key-pressed
    #()
    finish-animation)
  (start-rotation-animation))