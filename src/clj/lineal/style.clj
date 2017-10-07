(ns lineal.style
  (:require [garden.def :refer [defstylesheet defstyles defkeyframes]]
            [garden.units :as u :refer [px]]
            [garden.color :as c :refer [hex->hsl hsl->hex hsl hsla]] ;:rename {hex->rgb hr, rgb->hex rh}]
            [garden.selectors :as s :refer [nth-child defpseudoclass defpseudoelement]]
            ))

(defstyles lineal
  [:span.term {:font-weight "600"
               :margin-right "1em"}]
  [:div.matrix-mult
   [:div {:display "inline-block"
          :margin "2em"}]]
  [:table.matrix {:border-style "solid"
                  :border-width "1px"
                  }
   [:th :td {:padding "5px"
             :border-style "solid"
             :border-width "1px"
             :min-width "2em"
             :text-align "center"}
    [:input {:max-width "5em"
             :min-width "3em"
             :text-align "center"}]]
   ])
