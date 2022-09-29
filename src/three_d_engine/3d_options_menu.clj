(ns three-d-engine.3d-options-menu
  (:require [cljfx.api :as fx]))

(defn options-window [{:keys [on-color-changed on-rotation-stop on-rotation-start]}]
  {:fx/type :stage
   :showing true
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :padding 50
                  :children [{
                                :fx/type :button
                                :text "Red"
                                :on-action (fn [_] (on-color-changed {:r 219 :g 80 :b 80}))
                              } {
                                :fx/type :button
                                :text "Green"
                                :on-action (fn [_] (on-color-changed {:r 62 :g 126 :b 88}))
                              } {
                                :fx/type :button
                                :text "White"
                                :on-action (fn [_] (on-color-changed {:r 255 :g 255 :b 255}))
                              } {
                                :fx/type :button
                                :text "Stop rotation"
                                :on-action (fn [_] (on-rotation-stop))
                              }
                             {
                                :fx/type :button
                                :text "Continue rotation"
                                :on-action (fn [_] (on-rotation-start))
                              }
                             ]}}})

(def options-renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type options-window)))

(defn display-options-menu [on-color-changed on-rotation-stop on-rotation-start]
  (options-renderer {:fx/type options-window
                     :on-color-changed on-color-changed
                     :on-rotation-stop on-rotation-stop
                     :on-rotation-start on-rotation-start}))
