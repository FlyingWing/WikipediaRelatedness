'''
Original code kindly provided by Francesco.
'''

import logging
import json
import itertools
import multiprocessing
import os

from progressbar import ProgressBar

from gensim.utils import RULE_KEEP, RULE_DEFAULT, smart_open, to_unicode
from gensim.models.word2vec import LineSentence
from gensim.models.doc2vec import Doc2Vec, TaggedDocument

from w2v_utils import d2v_filename
from w2v_utils import wikicorpus_filename


class JsonCorpus(LineSentence):


    def __iter__(self):

        n = 100
        with smart_open(self.source) as fin:
            for line in itertools.islice(fin, self.limit):

                n -= 1
                if n == 0:
                    break

                line = unicode(line.strip(), errors='replace') # to_unicode(line.strip())  # check me
                document = json.loads(line)

                tokens = [token for sentence in document['sentences'] for token in sentence.split()]
                id = int(document['wikiId'])

                yield TaggedDocument(tokens, [id])



def get_wiki_ids(filename):
    wiki_ids = []
    logging.info('Loading WikiIDs...')
    bar = ProgressBar()
    n = 100
    with smart_open(filename) as fin:
        for line in bar(fin):
            document = json.loads(line)
            wiki_ids.append(int(document['wikiId']))
            n -= 100

            if n == 0:
                break

    return set(wiki_ids)



def trim_rule(word, count, min_count):
    if word.lower().startswith("ent_"):
        return RULE_KEEP
    else:
        return RULE_DEFAULT


def train_doc2vec(size, dm, outfilename):
    wiki_filename = wikicorpus_filename()

    model = Doc2Vec(
        size=size,
        workers=multiprocessing.cpu_count(),
        documents=JsonCorpus(wiki_filename, limit=None),  # check limit default,
        trim_rule=trim_rule,
        dm=dm
    )

    model.save(outfilename)  # commentme
    return model


def map_doc2vec(outfilename):
    model = Doc2Vec.load(outfilename) #, unicode_errors='ignore', binary=True)

    mapped_doc2vec_filename = os.path.join( '/'.join(outfilename.split('/')[0:-1]) , 'notstored_mapping_' + outfilename.split('/')[-1])
    logging.debug('Mapping documents into {0} file...'.format(mapped_doc2vec_filename))

    wiki_ids = get_wiki_ids(wikicorpus_filename())

    with smart_open(mapped_doc2vec_filename, 'wb') as f:
        for wiki_id in wiki_ids:
            print wiki_id
            print model
            vec = model.docvecs[wiki_id]

            print '---------'
            str_vec = ' '.join(['{1:.10f}'.format(v) for v in vec])

            f.write('ent_{0} '.format(wiki_id) + str_vec + '\n')

    logging.debug("Document mapped.")



def oldmap_doc2vec(model): #outfilename):
    #model = Doc2Vec.load(outfilename) #, unicode_errors='ignore', binary=True)

    #mapped_doc2vec_filename = os.path.join( '/'.join(outfilename.split('/')[0:-1]) , 'notstored_mapping_' + outfilename.split('/')[-1])
    #print mapped_doc2vec_filename
    #logging.debug('Mapping documents into {0} file...'.format(mapped_doc2vec_filename))

    wiki_ids = get_wiki_ids(wikicorpus_filename())

    with smart_open(mapped_doc2vec_filename, 'wb') as f:
        for wiki_id in wiki_ids:
            print wiki_id
            print model
            vec = model.docvecs[wiki_id]

            print '---------'
            str_vec = ' '.join(['{1:.10f}'.format(v) for v in vec])

            f.write('ent_{0} '.format(wiki_id) + str_vec + '\n')

    logging.debug("Document mapped.")


def generate_doc2vec_embeddings(size, train_algo):

    dm = {
        'pv-dm': 1,
        'pv-dbow': 0
    }[train_algo.lower()]

    outfilename = d2v_filename(size, train_algo)

    logging.info('Training Doc2Vec embeddings with size {0} and training algorithm {1}'.format(size, train_algo))
    model = train_doc2vec(int(size), dm, outfilename)

    logging.info('Mapping documents into Doc2Vec embeddings...')
    d2v_filename(size, train_algo)
    map_doc2vec(model)

