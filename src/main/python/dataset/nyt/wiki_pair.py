'''
WikiPair classes module.
Given a GoogleDataset, call build_wiki_pairs method.
'''

import sys
import os
import logging

from freebase_wikipedia import fb_wiki_title
from freebase_wikipedia import fb_wiki_id


logger = logging.getLogger('WikiPair')


class WikiEntity:

    def __init__(self, google_entity):
        self.wiki_title = fb_wiki_title[google_entity.freebase_mid][0]
        self.wiki_id = fb_wiki_id[google_entity.freebase_mid][1]
        self.frequency = 0

    def __hash__(self):
        return hash(self.wiki_title)

    def __eq__(self, other):
        return self.wiki_title == other.wiki_title

    def __ne__(self, other):
        return not(self == other)

    def __str__(self):
        return '{0},"{1}",{2}'.format(self.wiki_id,self.wiki_title,self.frequency)



class WikiPair:

    def __init__(self, src_entity, dst_entity):
        self.src_entity = src_entity
        self.dst_entity = dst_entity
        self.co_occurrence = 0

    def ordered_tuple(self):
        mn = min(self.src_entity.wiki_title, self.dst_entity.wiki_title)
        mx = max(self.src_entity.wiki_title, self.dst_entity.wiki_title)
        return (mn, mx)

    def __hash__(self):
        return hash(self.ordered_tuple())

    def __eq__(self, other):
        return (self.src_entity == other.src_entity and self.dst_entity == other.dst_entity) or\
               (self.src_entity == other.dst_entity and self.dst_entity == other.src_entity)

    def __ne__(self, other):
        return not(self == other)

    def __str__(self):
        return "{0},{1},{2}".format(str(self.src_entity), str(self.dst_entity), self.co_occurrence)


class WikiPairs:

    def __init__(self):
        self.ss = {}
        self.ns = {}
        self.nn = {}
        self.entities = {}

    def __str__(self):
        return "SS: {0}, NS: {1}, NN: {2}".format(len(self.ss), len(self.ns), len(self.nn))

    def get_entity(self, google_entity):
        wiki_entity = WikiEntity(google_entity)
        if wiki_entity in self.entities:
            return self.entities[wiki_entity]

        self.entities[wiki_entity] = wiki_entity
        return wiki_entity


    def get_pair(self, src_entity, dst_entity, bucket):
        wiki_pair = WikiPair(src_entity, dst_entity)
        if wiki_pair in bucket:
            return bucket[wiki_pair]

        bucket[wiki_pair] = wiki_pair
        return wiki_pair


    def update_bucket(self, bucket, wiki_pair):
        if wiki_pair not in bucket:
            bucket[wiki_pair] = wiki_pair
        bucket[wiki_pair].co_occurrence += 1


    def add_pair(self, src_google_entity, dst_google_entity):
        src_salience = src_google_entity.salience
        dst_salience = dst_google_entity.salience

        # Wiki Entity + increasing frequency

        src_entity = self.get_entity(src_google_entity)
        dst_entity = self.get_entity(dst_google_entity)
        src_entity.frequency += 1
        dst_entity.frequency += 1

        bucket = None
        if src_salience == "1" and dst_salience == "1":
            bucket = self.ss

        elif src_salience == "1" and dst_salience == "0" or src_salience == "0" and dst_salience == "1":
            bucket = self.ns
        else:
            bucket = self.nn

        wiki_pair = self.get_pair(src_entity, dst_entity, bucket)
        self.update_bucket(bucket, wiki_pair)


def build_wiki_pairs(docs):
    '''
    :param NYT documents
    :return: WikiPairs
    '''
    logger.info('Generating NYT Salience Wikipedia pairs...')

    wiki_pairs = WikiPairs()

    for index, entities in enumerate([doc.entities for doc in docs]):

        combs = combinations(entities, 2)
        for pair in combs:
            try:
                wiki_pairs.add_pair(*pair)

            except KeyError:
                continue
            except Exception:
                logger.warning("Excpetion not managed... {0}".format(str(sys.exc_info()[0])))

        if index % 1000 == 0 and index != 0:
            logger.info("Percentage {0} documents processed...".format(index))

    return wiki_pairs