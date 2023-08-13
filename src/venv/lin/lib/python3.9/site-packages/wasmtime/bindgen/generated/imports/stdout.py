from ..imports import streams
from abc import abstractmethod
from typing import Protocol

OutputStream = streams.OutputStream
class Stdout(Protocol):
    @abstractmethod
    def get_stdout(self) -> OutputStream:
        raise NotImplementedError

