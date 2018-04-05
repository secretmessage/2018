#!/bin/sh 

cd ~/src/2018;
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://staging.liftrocket.com
git add .;
git commit -S;
git push -u origin --all;

