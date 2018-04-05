#!/bin/sh 

cd ~/src/2018;
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://staging.liftrocket.com
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://staging.liftrocket.com:5000;
git add .;
git commit -S;
git push -u origin --all;

