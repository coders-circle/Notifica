import re


def get_regex(searchstring):
    words = re.sub("[^\S]", " ", searchstring).split()
    regexstring = r'(' + '|'.join([re.escape(n) for n in words]) + ')'
    return regexstring
