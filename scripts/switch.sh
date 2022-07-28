
# switch.sh
#!/bin/bash
# Crawl current connected port of WAS
CURRENT_PORT=$(cat /home/ubuntu/service_url.inc  | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Nginx currently proxies to ${CURRENT_PORT}."

# Toggle port number
if [ ${CURRENT_PORT} -eq 8090 ]; then
    TARGET_PORT=8091
elif [ ${CURRENT_PORT} -eq 8091 ]; then
    TARGET_PORT=8090
else
    echo "> No WAS is connected to nginx"
    exit 1
fi

# Change proxying port into target port
echo "set \$service_url http://54.180.96.247:${TARGET_PORT};" | tee /home/ubuntu/service_url.inc

echo "> Now Nginx proxies to ${TARGET_PORT}."

# Reload nginx
sudo service nginx reload

echo "> Nginx reloaded."
