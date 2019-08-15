# -*- coding: utf-8 -*-
"""
Created on Wed Aug 14 11:33:13 2019

@author: fxue
"""

import requests

url = 'https://api.github.com/search/repositories?q=language:python&sort=stars'

r = requests.get(url)
result_dict = r.json()

print(r.status_code)
print(result_dict.keys())

project_list = result_dict['items']
print('Repository returned: ', len(project_list))

project = project_list[0]
print('Keys:', len(project))
for key in sorted(project.keys()):
    print(key)

