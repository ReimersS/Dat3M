{
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; config.allowUnfree = true; };
        FixedHashes = {
          x86_64-linux = "sha256-dK0GgR3A//wQ29jRlocWBxPp3o6tdxy2iUf6zTMgV9w=";
          aarch64-linux = "sha256-eieSmKQhjNB00YadzSku7jDwNcOollUTl7vWoOHtFMg=";
        };
        graal-mvn = pkgs.maven.override {
          jdk_headless = pkgs.graalvmPackages.graalvm-oracle;
        };
      in
      {
        packages.default = graal-mvn.buildMavenPackage rec {
          pname = "Dat3m";
          name = "dartagnan";
          src = self;
          version = "4.4.0-alpha";

          LD_LIBRARY_PATH = "${pkgs.stdenv.cc.cc.lib}/lib";

          nativeBuildInputs = with pkgs; [ makeWrapper graalvmPackages.graalvm-oracle clang ];
          mvnParameters = "-DskipTests";

          mvnHash = FixedHashes.${system};
          maven = graal-mvn;
          mvnJdk = pkgs.graalvmPackages.graalvm-oracle;

          installPhase = ''
            mkdir -p $out
            cp -r dartagnan $out/

            makeWrapper ${pkgs.graalvmPackages.graalvm-oracle}/bin/java $out/bin/${name} \
              --add-flags "-jar $out/dartagnan/target/${name}.jar"
          '';
        };
      }
    );
}
