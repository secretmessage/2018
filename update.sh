#!/bin/sh 

cd ~/src/2018;
cd ~/src/2018/venus;
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://venus.qc.cuny.edu;
git add .;
git commit -S;
git push -u origin --all;

