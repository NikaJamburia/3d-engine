(ns three-d-engine.3d-display
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-meshes :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-projection :refer :all]
            [three-d-engine.3d-rotation :refer :all]
            [three-d-engine.3d-util :refer :all])
  (:import (javax.swing JFrame JPanel Timer)
           (java.awt Dimension Color Polygon Toolkit)
           (java.awt.event ActionListener)))

(def start-millis (System/currentTimeMillis))
(def repaint-millis 30)
(def mesh-color (new Color 62 126 88))
(def display-wireframe false)

(defn adjust-color-part [part lighting]
  (let [abs-value (Math/abs (float lighting))
        multiplied (Math/ceil (* abs-value part))]
    (if (> multiplied 255) 255 (int multiplied))))

(defn adjust-color [color lighting]
  (if (neg? lighting)
    (new Color
         (adjust-color-part (.getRed color) lighting)
         (adjust-color-part (.getGreen color) lighting)
         (adjust-color-part (.getBlue color) lighting))
    (Color/BLACK)))

(defn draw-wireframe [g poly]
  (.setColor g (Color/BLACK))
  (.drawPolygon g poly))

(defn fill-triangle [triangle g]
  (let [xs (int-array (map #(:x %) (:vectors triangle)))
        ys (int-array (map #(:y %) (:vectors triangle)))
        poly (new Polygon xs ys 3)
        color (adjust-color mesh-color (:lighting triangle))]
    (if (= true display-wireframe) (draw-wireframe g poly) nil)
    (.setColor g color)
    (.fillPolygon g poly)))

(defn create-canvas [mesh]
  (proxy [JPanel] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (vec (map #(fill-triangle % g) (:triangles mesh))))
    (getPreferredSize [] (new Dimension (:width window-size) (:height window-size)))))

(defn mesh-to-display [mesh rotation-theta]
  (-> mesh
      (rotate-mesh rotation-theta)
      (project-to-3d)))

(defn repaint-canvas [top-panel canvas]
  (.removeAll top-panel)
  (.revalidate top-panel)
  (.repaint top-panel)
  (if (= (System/getProperty "os.name") "Linux")
     (.sync (Toolkit/getDefaultToolkit)))
  (.setBackground canvas (Color/BLACK))
  (.add top-panel canvas)
  (.setVisible top-panel true))

(defn repaint-action-listener [top-panel mesh]
  (proxy [ActionListener] []
    (actionPerformed [e]
      (let [time-elapsed (/ (- (.getWhen e) start-millis) 1500)]
        (repaint-canvas top-panel (create-canvas (mesh-to-display mesh time-elapsed)))))))

(defn -main[& args]
  (let [frame (new JFrame)
        top-panel (new JPanel)
        mesh (import-mesh-from "teapot.obj")
        ;mesh mesh-cube
        canvas (create-canvas (mesh-to-display mesh 0))
        timer (new Timer repaint-millis nil)]
    (.setPreferredSize frame (new Dimension (:width window-size) (:height window-size)))
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setBackground canvas (Color/BLACK))
    (.setPreferredSize top-panel (new Dimension (:width window-size) (:height window-size)))
    (.add top-panel canvas)
    (.add frame top-panel)
    (.pack frame)
    (.setVisible frame true)
    (.addActionListener timer (repaint-action-listener top-panel mesh))
    (.start timer)
    ))
