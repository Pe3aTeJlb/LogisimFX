from ..imports import streams
from abc import abstractmethod
from typing import Protocol

InputStream = streams.InputStream
class Stdin(Protocol):
    @abstractmethod
    def get_stdin(self) -> InputStream:
        raise NotImplementedError

