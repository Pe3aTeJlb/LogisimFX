from ..imports import filesystem
from abc import abstractmethod
from typing import List, Protocol, Tuple

Descriptor = filesystem.Descriptor
class Preopens(Protocol):
    @abstractmethod
    def get_directories(self) -> List[Tuple[Descriptor, str]]:
        raise NotImplementedError

