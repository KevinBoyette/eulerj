{
  description = "Project Euler solutions in Java";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    { self, nixpkgs }:
    let
      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];
      forAllSystems = f: nixpkgs.lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});
    in
    {
      devShells = forAllSystems (pkgs: {
        default = pkgs.mkShell {
          packages = [
            pkgs.jdk
            pkgs.google-java-format
          ];
        };
      });

      # `nix fmt` formats every Java file in the repo with google-java-format.
      formatter = forAllSystems (
        pkgs:
        pkgs.writeShellApplication {
          name = "fmt";
          runtimeInputs = [
            pkgs.findutils
            pkgs.google-java-format
          ];
          text = ''
            if [ "$#" -gt 0 ]; then
              google-java-format --replace "$@"
            else
              find . -name '*.java' -not -path './.git/*' -exec google-java-format --replace {} +
            fi
          '';
        }
      );

      # `nix flake check` compiles every problem, runs it (each main() asserts
      # its own answer), and verifies formatting.
      checks = forAllSystems (pkgs: {
        problems =
          pkgs.runCommand "eulerj-problems"
            {
              src = self;
              nativeBuildInputs = [ pkgs.jdk ];
            }
            ''
              cp $src/*.java .
              javac -d classes *.java
              for f in Problem*.java; do
                name="''${f%.java}"
                echo "== $name"
                java -cp classes "$name"
              done
              touch $out
            '';

        format =
          pkgs.runCommand "eulerj-format"
            {
              src = self;
              nativeBuildInputs = [ pkgs.google-java-format ];
            }
            ''
              google-java-format --dry-run --set-exit-if-changed $src/*.java
              touch $out
            '';
      });
    };
}
