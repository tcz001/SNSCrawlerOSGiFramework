rm -r ./felix-cache/;
find ~/Github/SNSCrawlerOSGiFramework/*/build/libs/ -name "*.jar" -exec cp {} ./bundle/ \;
java -jar bin/felix.jar ;
