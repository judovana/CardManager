#!/bin/bash
. /usr/share/java-utils/java-functions

MAIN_CLASS=cardmanager.gui.PlayTable

set_classpath "CardManager"

mem=`free -mt | tail -n1 | sed 's/  */ /g' | cut -d ' ' -f 2`;
let "mem=$mem/3*2"
r=`let "mem=$mem/3*2" 2>&1`; 
echo $mem
echo "$r" | r=`sed 's/  */ /g'`
echo $r

DATA=data
BGS=backgrounds
CLS=collection
CLASS=classicCards
PKGS=packages
NOIMAGE=noimage.jpg
CM=CardManager
BG1=b1fv.png
BG2=b2fv.png
P1=clasic32
P2=fullCanasta

USER_DIR=~/$CM
UDATA_DIR=$USER_DIR/$DATA

INSTALL_DIR=/usr/share/$CM/
IDATA_DIR=$INSTALL_DIR/$DATA

if [ -f $USER_DIR ] ; then 
  echo "This applicatio needs directory $USER_DIR but it exists and is file. Application can not continue";
  exit 5;
fi
if [ -d $USER_DIR ] ; then 
  echo "$USER_DIR already exists, using"
else
  mkdir $USER_DIR
  mkdir $UDATA_DIR
  mkdir $UDATA_DIR/$BGS
  mkdir $UDATA_DIR/$PKGS
  mkdir $USER_DIR/$CLS

  ln -s $IDATA_DIR/$NOIMAGE  $UDATA_DIR/$NOIMAGE
  ln -s $INSTALL_DIR/$CLS/$CLASS  $USER_DIR/$CLS/$CLASS
  ln -s $IDATA_DIR/$PKGS/$P1  $UDATA_DIR/$PKGS/$P1
  ln -s $IDATA_DIR/$PKGS/$P2  $UDATA_DIR/$PKGS/$P2

  ln -s $IDATA_DIR/$BGS/$BG1  $UDATA_DIR/$BGS/$BG1
  ln -s $IDATA_DIR/$BGS/$BG2  $UDATA_DIR/$BGS/$BG2
fi;

pushd $USER_DIR

if [ "$r" = "" ] ; then 
  FLAGS=$FLAGS" -Xmx$mem""M"
  echo "executing with -Xmx$mem""M"
  run "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"
else
  echo "executing without -Xmx!"
  run "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"
fi

popd

