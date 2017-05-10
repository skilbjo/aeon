(defproject compojure "0.1.0-SNAPSHOT"
  :uberjar-name "compojure.jar"
  :url "https://github.com/skilbjo/composure"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.3.0"]
                 [clj-time "0.12.2"]
                 [compojure "1.5.1"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [environ "1.1.0"]
                 [org.clojure/java.jdbc "0.5.8"]
                 [org.postgresql/postgresql "42.0.0"]
                 [ring "1.1.6"]
                 [ring/ring-defaults "0.2.1"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-ring "0.9.7"]]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]}
             :uberjar {:aot :all}}
  :target-path "target/%s"
  :main ^:skip-aot handler
  :ring {:handler handler/app
         :port 8080}
  :jvm-opts ["-Xms256m" "-Xmx256m" "-XX:MaxMetaspaceSize=128m"
             "-client" "-Duser.timezone=PST8PDT"
             "-Dclojure.compiler.direct-linking=true"
             "-XX:-OmitStackTraceInFastThrow"])
