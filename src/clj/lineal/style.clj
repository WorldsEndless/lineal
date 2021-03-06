(ns lineal.style
  (:require [garden.def :refer [defstylesheet defstyles defkeyframes]]
            [garden.units :as u :refer [px]]
            [garden.color :as c :refer [hex->hsl hsl->hex hsl hsla]] ;:rename {hex->rgb hr, rgb->hex rh}]
            [garden.selectors :as s :refer [nth-child defpseudoclass defpseudoelement]]))

(defstyles lineal
  [:span.term {:font-weight "600"
               :margin-right "1em"}]
  [:.text-center {:text-align "center"}]
  [:div.inputs {:border "solid red 2px"}]
  [:div.row-control
   {:text-align "right"}]
  [:div.stack
   {:display "table"}
   [:div {:float "none"
          :width "1em"
          :margin "0"}]
   [:div.row {:display "table-row"}
    [:div {:display "table-cell"}]]
   [:div.explanation {:transform "rotate(270deg)"
                      :transform-origin "middle bottom 0"
                      :height "3em"
                      :vertical-align "bottom"
                      }]]
  [:div.matrix {:padding 0}]
  [:div.increase :div.decrease {:padding 0}]
  [:table.matrix {:border-style "solid"
                :border-width "1px"
                :margin "auto"
                  ;:display "inline-block"
                  }
   [:th :td {:padding "5px"
             :border-style "solid"
             :border-width "1px"
             :min-width "2em"
             :text-align "center"}
    [:input {:max-width "5em"
             :min-width "3em"
             :text-align "center"}]]
   ]
  )
