(ns three-d-engine.3d-import-test
  (:require [clojure.test :refer :all]
            [three-d-engine.3d-import :refer :all]
            [three-d-engine.3d-core :refer :all]
            [clojure.java.io :as io]))

(defn get-lines [file-path]
  (with-open [file (io/reader (io/input-stream (io/resource file-path)))]
    (vec (line-seq file))))

(deftest collect-vertices-test
  (testing "all lines starting with 'v ' are collected"
    (let [lines (get-lines "test-obj.obj")
          result (vec (collect-vectors lines))]
      (is (= 6 (count result)))
      (are [a x y z] (and
                   (= (:x a) x)
                   (= (:y a) y)
                   (= (:z a) z))
                     (get result 0) 1.000000 -1.000000 -1.000000
                     (get result 1) 1.000000 1.000000 -1.000000
                     (get result 2) 1.000000 -1.000000 1.000000
                     (get result 3) 1.000000 1.000000 1.000000
                     (get result 4) -1.000000 -1.000000 -1.000000
                     (get result 5) -1.000000 1.000000 -1.000000)
      )))

(deftest collect-triangles-test
  (testing "all lines starting with 'f ' are collected"
    (let [lines (get-lines "test-obj.obj")
          result (vec (collect-triangles lines (vec (collect-vectors lines))))]
      (is (= 2 (count result)))
      (are [a pts] (= (:vectors a) pts)
                   (get result 0) [(vector-3d 1.000000 -1.000000 -1.000000)
                                     (vector-3d 1.000000 1.000000 -1.000000)
                                     (vector-3d 1.000000 -1.000000 1.000000)]

                   (get result 1) [(vector-3d 1.000000 1.000000 1.000000)
                                     (vector-3d -1.000000 -1.000000 -1.000000)
                                     (vector-3d -1.000000 1.000000 -1.000000)])
      )))

(deftest import-test
  (testing "takes all the data from obj file and creates a mesh"
    (let [result (import-mesh-from "test-obj.obj")]
      (is (= 2 (count (:triangles result))))
      (are [a pts] (= (:vectors a) pts)
                   (get (:triangles result) 0) [(vector-3d 1.000000 -1.000000 -1.000000)
                                   (vector-3d 1.000000 1.000000 -1.000000)
                                   (vector-3d 1.000000 -1.000000 1.000000)]

                   (get (:triangles result) 1) [(vector-3d 1.000000 1.000000 1.000000)
                                   (vector-3d -1.000000 -1.000000 -1.000000)
                                   (vector-3d -1.000000 1.000000 -1.000000)])
      )))
