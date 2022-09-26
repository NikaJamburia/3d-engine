(ns three-d-engine.3d-meshes
  (:require [three-d-engine.3d-core :refer :all]))

(def mesh-cube
  (create-mesh [
                ;SOUTH
                (create-triangle [(vector-3d 0 0 0) (vector-3d 0 1 0) (vector-3d 1 1 0)])
                (create-triangle [(vector-3d 0 0 0) (vector-3d 1 1 0) (vector-3d 1 0 0)])

                ;EAST
                (create-triangle [(vector-3d 1 0 0) (vector-3d 1 1 0) (vector-3d 1 1 1)])
                (create-triangle [(vector-3d 1 0 0) (vector-3d 1 1 1) (vector-3d 1 0 1)])

                ;NORTH
                (create-triangle [(vector-3d 1 0 1) (vector-3d 1 1 1) (vector-3d 0 1 1)])
                (create-triangle [(vector-3d 1 0 1) (vector-3d 0 1 1) (vector-3d 0 0 1)])

                ;WEST
                (create-triangle [(vector-3d 0 0 1) (vector-3d 0 1 1) (vector-3d 0 1 0)])
                (create-triangle [(vector-3d 0 0 1) (vector-3d 0 1 0) (vector-3d 0 0 0)])

                ;TOP
                (create-triangle [(vector-3d 0 1 0) (vector-3d 0 1 1) (vector-3d 1 1 1)])
                (create-triangle [(vector-3d 0 1 0) (vector-3d 1 1 1) (vector-3d 1 1 0)])

                ;BOTTOM
                (create-triangle [(vector-3d 1 0 1) (vector-3d 0 0 1) (vector-3d 0 0 0)])
                (create-triangle [(vector-3d 1 0 1) (vector-3d 0 0 0) (vector-3d 1 0 0)])
                ]))

(def mesh-pyramid
  (let [tip-coords 0.5
        tip (vector-3d tip-coords 1 tip-coords)]
    (create-mesh [
                  ;BOTTOM
                  (create-triangle [(vector-3d 1 0 1) (vector-3d 0 0 1) (vector-3d 0 0 0)])
                  (create-triangle [(vector-3d 1 0 1) (vector-3d 0 0 0) (vector-3d 1 0 0)])

                  ;SOUTH
                  (create-triangle [(vector-3d 0 0 0) tip (vector-3d 1 0 0)])

                  ;EAST
                  (create-triangle [(vector-3d 1 0 0) tip (vector-3d 1 0 1)])

                  ;NORTH
                  (create-triangle [(vector-3d 1 0 1) tip (vector-3d 0 0 1)])

                  ;WEST
                  (create-triangle [(vector-3d 0 0 1) tip (vector-3d 0 0 0)])
                  ])))

(def mesh-pyramid-triangular
  (let [tip-coords 0.25
        tip (vector-3d tip-coords 1 tip-coords)]
    (create-mesh [
                  ;BOTTOM
                  (create-triangle [(vector-3d 0.5 0 0.5) (vector-3d 0 0 0) (vector-3d 1 0 0)])

                  ;SOUTH
                  (create-triangle [(vector-3d 1 0 0) tip (vector-3d 0 0 0)])

                  ;EAST
                  (create-triangle [(vector-3d 0.5 0 0.5) tip (vector-3d 1 0 0)])

                  ;NORTH
                  (create-triangle [(vector-3d 0 0 0) tip (vector-3d 0.5 0 0.5)])
                  ])))

