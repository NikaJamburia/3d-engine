(ns three-d-engine.3d-import
  (:require [three-d-engine.3d-core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [three-d-engine.3d-util :refer :all]))

(defn parse-obj-line [line]
  (let [split (str/split line #" ")
        just-nums (subvec split 1)]
    (vec (map #(Float/parseFloat %) just-nums))))

(defn collect-vectors [lines]
  (->> lines
          (filter #(str/starts-with? % "v"))
          (map #(parse-obj-line %))
          (map #(vector-3d
                  (first %)
                  (second %)
                  (third % )))
          (vec)))

(defn get-vectors [indexes vectors]
  (vec (map #(get vectors (dec (int %))) indexes)))

(defn collect-triangles [lines vectors]
  (->> lines
       (filter #(str/starts-with? % "f"))
       (map #(parse-obj-line %))
       (map #(get-vectors % vectors))
       (map #(create-triangle %))))

(defn import-mesh-from [filename]
  (with-open [file (io/reader (io/input-stream (io/resource filename)))]
    (let [lines (line-seq file)
          vectors (collect-vectors lines)
          tris (collect-triangles lines vectors)]
      (create-mesh (vec tris)))))
