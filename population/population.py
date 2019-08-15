# -*- coding: utf-8 -*-
"""
Created on Wed Aug 14 10:57:57 2019

@author: fxue
"""

import json
from country_codes import get_country_code
import pygal_maps_world.maps

filename = 'population_data.json'

world_populations = {}

with open(filename) as f:
    data = json.load(f)
    for pop_dict in data:
        if pop_dict['Year'] == '2010':
            country_name = pop_dict['Country Name']
            population = int(float(pop_dict['Value']))
            code = get_country_code(country_name)
            if code:
                world_populations[code] = population

wm = pygal_maps_world.maps.World()
wm.title = 'World Population in 2010, by Country'

wm.add('2010', world_populations)
wm.render_to_file('world_popuation.svg')
