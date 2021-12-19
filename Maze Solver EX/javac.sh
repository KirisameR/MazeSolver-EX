find . -name "*.class" -type f -delete

javac -d ./bin --module-path ./lib/ --add-modules=javafx.controls,javafx.fxml --source-path ./src  $1
