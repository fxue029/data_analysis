# -*- coding: utf-8 -*-
"""
Created on Wed Aug 14 14:20:07 2019

@author: fxue
"""

import requests
from operator import itemgetter

url = 'https://hacker-news.firebaseio.com/v0/topstories.json'
r = requests.get(url)
print('Status code: ', r.status_code)

top_ids = r.json()

articles = []
for  id in top_ids[:30]:
    url = 'https://hacker-news.firebaseio.com/v0/item/'+str(id)+'.json'
    #print(url)
    response = requests.get(url)
    #print(response.status_code)
    article = response.json()
    #print(article.keys())
    article_dict = {
            'title':article['title'],
            'link':'https://news.ycombinator.com/item?id='+str(id),
            'comments': article.get('descendants', 0)
            }
    articles.append(article_dict)

articles = sorted(articles, key=itemgetter('comments'), reverse=True)


#names, comments = [],[]
#for a in articles:
#    print('title: ', a['title'])
#    print('link: ', a['link'])
#    print('comments: ', a['comments'])
#    print()
#    names.append(a['title'])
#    comments.append(a['comments'])
#
#import pygal
#from pygal.style import LightColorizedStyle as LCS, LightenStyle as LS
#my_style = LS('#333366', base_style=LCS)
#chart = pygal.Bar(style=my_style, x_label_rotation=45, show_legend=False)
#chart.title = 'Hacker News Top Articles'
#chart.x_labels = names
#chart.add('', comments)
#chart.render_to_file('hn_articles.svg')

names, plot_dicts = [],[]
for a in articles:
    names.append(a['title'])
    plot_dict = {
            'value':a['comments'],
            'label':a['title'],
            'xlink':a['link']
            }
    plot_dicts.append(plot_dict)

import pygal
from pygal.style import LightColorizedStyle as LCS, LightenStyle as LS
my_style = LS('#333366', base_style=LCS)
chart = pygal.Bar(style=my_style, x_label_rotation=45, show_legend=False)
chart.title = 'Hacker News Top Articles'
chart.x_labels = names
chart.add('', plot_dicts)
chart.render_to_file('hn_articles_detail.svg')
    
