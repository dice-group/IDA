var count = 0;

function Id(id) {
    this.id = id;
}

export default function (name) {
    return new Id("O-" + (name == null ? "" : name + "-") + ++count);
}
