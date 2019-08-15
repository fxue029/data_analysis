# -*- coding: utf-8 -*-
"""
Created on Tue Aug 13 11:34:56 2019

@author: fxue
"""

import matplotlib.pyplot as plt

from random_walk import RandomWalk

rw = RandomWalk()
rw.fill_walk()

point_nums = list(range(rw.num_points))

plt.figure(figsize=(10,6))

plt.scatter(rw.x_values, rw.y_values, c=point_nums, cmap=plt.cm.Blues, edgecolor='none', s=15)

plt.scatter(rw.x_values[0], rw.y_values[0], c='red', edgecolors='none', s=100)
plt.scatter(rw.x_values[-1], rw.y_values[-1], c='red', edgecolors='none', s=100)

plt.show()