(ns todo.logging
  (:require [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [potemkin :refer [import-vars]]))

(import-vars [taoensso.timbre
              log trace debug info warn error fatal report
              logf tracef debugf infof warnf errorf fatalf reportf
              spy get-env log-env])

(defn- configure-logging [{:keys [log-level log-file max-log-size]}]
  (timbre/set-level! (or log-level :debug))
  (when log-file
    (timbre/merge-config! {:shared-appender-config
                           {:rotor
                            {:path log-file
                             :max-size (or max-log-size (* 512 1024))
                             :backlog 5}}})
    (timbre/merge-config! {:appenders
                           {:rotor rotor/rotor-appender}})
    (timbre/merge-config! {:appenders
                           {:standard-out
                            {:enabled? false}}})))

(defn init!
  "Initialize logging."
  [config]
  (configure-logging config))
