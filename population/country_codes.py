# -*- coding: utf-8 -*-
"""
Created on Wed Aug 14 11:10:41 2019

@author: fxue
"""

from pygal_maps_world.i18n import COUNTRIES


def get_country_code(country_name):
    for code, name in COUNTRIES.items():
        if name == country_name:
            return code
    return None