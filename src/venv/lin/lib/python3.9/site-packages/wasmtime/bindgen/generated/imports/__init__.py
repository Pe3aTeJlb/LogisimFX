from .environment import Environment
from .exit import Exit
from .filesystem import Filesystem
from .preopens import Preopens
from .random import Random
from .stderr import Stderr
from .stdin import Stdin
from .stdout import Stdout
from .streams import Streams
from dataclasses import dataclass

@dataclass
class RootImports:
    streams: Streams
    filesystem: Filesystem
    random: Random
    environment: Environment
    preopens: Preopens
    exit: Exit
    stdin: Stdin
    stdout: Stdout
    stderr: Stderr
