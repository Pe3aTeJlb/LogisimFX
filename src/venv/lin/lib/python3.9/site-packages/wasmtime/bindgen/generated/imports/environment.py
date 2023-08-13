from abc import abstractmethod
from typing import List, Protocol, Tuple

class Environment(Protocol):
    @abstractmethod
    def get_environment(self) -> List[Tuple[str, str]]:
        raise NotImplementedError

