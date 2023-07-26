from ..types import Result
from abc import abstractmethod
from dataclasses import dataclass
from typing import Protocol

InputStream = int
OutputStream = int
@dataclass
class StreamError:
    pass

class Streams(Protocol):
    @abstractmethod
    def drop_input_stream(self, this: InputStream) -> None:
        raise NotImplementedError
    @abstractmethod
    def write(self, this: OutputStream, buf: bytes) -> Result[int, StreamError]:
        raise NotImplementedError
    @abstractmethod
    def blocking_write(self, this: OutputStream, buf: bytes) -> Result[int, StreamError]:
        raise NotImplementedError
    @abstractmethod
    def drop_output_stream(self, this: OutputStream) -> None:
        raise NotImplementedError

