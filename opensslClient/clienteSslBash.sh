#!/bin/bash
parallel -j 100 < comandosInit.txt
parallel -j 100 < comandosContador1.txt
parallel -j 100 < comandosContador2.txt
parallel -j 100 < comandosContador3.txt
