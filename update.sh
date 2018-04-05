#!/bin/sh 

cd ~/src/2018;
cd ~/src/2018/techmeme;
wget --no-proxy --recursive --no-parent -w 3 --random-wait https://techmeme.com;
git add .;
git commit -S;
git push -u origin --all;

