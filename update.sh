#!/bin/sh 

cd ~/src/2018/780-35/website;
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://qc-csdept-780:UMLobject-332-class@picasso.cs.qc.cuny.edu/cs780/;
cd ~/src/2018/765-28/website;
wget --no-proxy --recursive --no-parent -w 3 --random-wait http://venus.cs.qc.cuny.edu/~smane/;
cd ~/src/2018;
git add -A;
git commit -m "add website changes";
git pull origin master;
git push -u origin --all;
git add .;
git commit -S;
git push -u origin --all;

