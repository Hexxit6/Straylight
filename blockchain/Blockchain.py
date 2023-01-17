from datetime import datetime
from hashlib import sha256


class Block:
    def __init__(self, index, timestamp, data, prev_hash, diff, nonce):
        self.index      = index
        self.timestamp  = timestamp
        self.data       = data
        self.prev_hash  = prev_hash
        self.diff       = diff
        self.nonce      = nonce
        self.hash       = self.calc_hash()

    def __str__(self):
        return (f"Index:         {self.index}\n"
                f"Timestamp:     {self.timestamp}\n"
                f"Data:          {self.data}\n"
                f"Hash           {self.hash}\n"
                f"Previous hash: {self.prev_hash}\n"
                f"Difficulty:    {self.diff}\n"
                f"Nonce:         {self.nonce}")

    def calc_hash(self):
        hash = sha256()
        hash.update(str(self.index).encode() +
                    str(self.timestamp).encode() +
                    str(self.data).encode() +
                    str(self.prev_hash).encode() +
                    str(self.diff).encode() +
                    str(self.nonce).encode())
        return hash.hexdigest()


class Blockchain:
    def __init__(self):
        self.difficulty = 4
        self.generation_time = 10
        self.diff_interval = 10
        self.chain = [ self.create_genesis() ]

    def create_genesis(self):
        return Block(0, datetime.utcnow(), "Straylight", "0"*64, self.difficulty, 0)

    def add_block(self, block):
        block.prev_hash = self.chain[-1].hash
        block.hash = block.calc_hash()
        self.chain.append(block)

    def is_block_valid(self, prev_block, block) -> bool:
        if block.index != prev_block.index+1 or \
           block.prev_hash != prev_block.hash or \
           block.calc_hash() != block.hash:
            return False
        return True

    def is_chain_valid(self) -> bool:
        for block, prev_block in zip(self.chain[1:], self.chain[:-1]):
            if not self.is_block_valid(prev_block, block):
                return False
        return True

    def calc_chain_diff(self, chain) -> int:
        return sum([ pow(2, block.diff) for block in chain ])

    def update(self, other_chain) -> bool:
        """
        If chains aren't the same, calculate the cumulative sum for the divergent parts of the chains.
        Set the current chain to the other_chain if the other_chain has a greater value.
        Return True if the chain was updated.
        """
        if self.chain == other_chain:
            return False
        for index, (a,b) in enumerate(zip(self.chain, other_chain)):
            if a != b:
                break
        if self.calc_chain_diff(self.chain[index:]) < self.calc_chain_diff(other_chain[index:]):
            self.chain = other_chain
            return True
        return False

    def adjust_difficulty(self):
        if len(self.chain) > self.diff_interval:
            prev_adj_block = self.chain[-self.diff_interval]
            time_expected = self.generation_time * self.diff_interval
            time_taken = (self.chain[-1].timestamp - prev_adj_block.timestamp).seconds

            if time_taken < (time_expected//2):
                self.difficulty = prev_adj_block.diff + 1
            elif time_taken > (time_expected*2):
                self.difficulty = prev_adj_block.diff - 1
            else:
                self.difficulty = prev_adj_block.diff
