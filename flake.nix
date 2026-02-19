{
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        FixedHashes = {
          x86_64-linux = "sha256-dK0GgR3A//wQ29jRlocWBxPp3o6tdxy2iUf6zTMgV9w=";
          aarch64-linux = "sha256-t837WYusF1ePHgHXY3lmP+wESe/GU6QtjEgNkv4zOy0=";
        };
      in
      {
        packages.default = pkgs.maven.buildMavenPackage rec {
          pname = "Dat3m";
          name = "dartagnan";
          src = self;
          version = "4.4.0-alpha";

          nativeBuildInputs = with pkgs; [ makeWrapper jre25_minimal jdk25_headless clang ];
          mvnParameters = "-DskipTests";

          mvnHash = FixedHashes.${system};

          installPhase = ''
            mkdir -p $out
            cp -r dartagnan $out/

            makeWrapper ${pkgs.jdk25_headless}/bin/java $out/bin/${name} \
              --add-flags "-jar $out/dartagnan/target/${name}.jar"
          '';
        };
      }
    );
}
