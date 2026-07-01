import random

def random_ip():

    return ".".join(str(random.randint(1,255)) for _ in range(4))